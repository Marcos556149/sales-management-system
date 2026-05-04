import { apiClient } from '../api/client';

export const configService = {
  /**
   * Retrieves the global system configuration.
   * @param {Object} options - Optional fetch options (e.g., signal for aborting)
   * @returns {Promise<Object>} The system configuration response DTO
   */
  getConfig: async (options = {}) => {
    const response = await apiClient.get('/api/configuration', options);
    return response;
  },

  /**
   * Updates the global system configuration.
   * @param {Object} payload - The configuration to update { businessName, businessAddress }
   * @param {Object} options - Optional fetch options
   * @returns {Promise<Object>} The updated system configuration
   */
  updateConfig: async (payload, options = {}) => {
    const response = await apiClient.put('/api/configuration', payload, options);
    return response;
  }
};
