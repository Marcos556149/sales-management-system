import { apiClient } from '../api/client';

/**
 * Utility to print a sale ticket using the browser's print functionality.
 * Designed for thermal printers (monospace, narrow width).
 * 
 * @param {number|string} saleId - The ID of the sale to print.
 * @returns {Promise<boolean>} - Resolves to true if the process started successfully.
 */
export const printTicket = async (saleId) => {
  if (!saleId) return false;

  try {
    const response = await apiClient.get(`/api/sales/${saleId}/ticket`);
    const ticketText = response.data + "\n\n\n\n\n";

    if (!ticketText) {
      throw new Error("Received empty ticket content from server");
    }

    // Create a hidden iframe for printing
    const iframe = document.createElement('iframe');
    iframe.style.position = 'fixed';
    iframe.style.right = '0';
    iframe.style.bottom = '0';
    iframe.style.width = '0';
    iframe.style.height = '0';
    iframe.style.border = 'none';
    document.body.appendChild(iframe);

    const doc = iframe.contentWindow.document;
    doc.open();
    doc.write(`
      <html>
        <head>
          <title>Sale Ticket ${saleId}</title>
          <style>
            @page {
              size: 58mm auto;
              margin: 0;
            }
            body {
              margin: 0;
              padding: 0;
              background-color: white;
            }
            pre {
              margin: 0;
              padding: 0;
              font-family: monospace;
              font-size: 12px;
              line-height: 1.2;
              white-space: pre;
              width: 32ch;
              overflow: hidden;
            }
          </style>
        </head>
        <body>
          <pre>${ticketText}</pre>
        </body>
      </html>
    `);
    doc.close();

    // Give the browser a moment to render the content in the iframe
    return new Promise((resolve) => {
      setTimeout(() => {
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
        
        // Remove the iframe after some delay to allow the print dialog to open
        setTimeout(() => {
          document.body.removeChild(iframe);
          resolve(true);
        }, 1000);
      }, 500);
    });
  } catch (error) {
    console.error("Print Error:", error);
    throw error;
  }
};
