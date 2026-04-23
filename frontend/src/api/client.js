/**
 * Centralized API Client
 * Handles fetching, error normalization, and response unwrapping.
 */

const normalizeError = (errorData, status) => {
  // New standard error format
  if (errorData?.error && typeof errorData.error === 'object') {
    const errObj = errorData.error;
    if (errObj.field) {
      return `${errObj.code || 'Validation Error'} in ${errObj.field}: ${errObj.message}`;
    }
    return errObj.message || `Error: ${errObj.code}`;
  }
  
  // Old error format
  if (errorData?.error && typeof errorData.error === 'string') {
    return errorData.error;
  }
  if (errorData?.message) {
    return errorData.message;
  }
  
  return `Error: ${status}`;
};

const handleResponse = async (response) => {
  let responseData;
  const contentType = response.headers.get('content-type');
  
  if (contentType && contentType.includes('application/json')) {
    responseData = await response.json().catch(() => ({}));
  } else {
    responseData = await response.text().catch(() => '');
    try {
      // Sometimes it's JSON but without the header
      responseData = JSON.parse(responseData);
    } catch (e) {
      // Keep as text
    }
  }

  if (!response.ok) {
    const errorMessage = normalizeError(responseData, response.status);
    const error = new Error(errorMessage);
    error.status = response.status;
    error.details = responseData;
    throw error;
  }

  // Handle new standard success format { code, message, data }
  if (responseData && typeof responseData === 'object' && 'data' in responseData && 'code' in responseData) {
    return {
      data: responseData.data,
      message: responseData.message,
      code: responseData.code,
      isWrapped: true
    };
  }

  // Legacy format: return directly but wrap it similarly so services can handle it consistently
  // If it's paginated (e.g., has 'content', 'totalPages'), it's unwrapped.
  return {
    data: responseData,
    message: null,
    isWrapped: false
  };
};

export const apiClient = {
  get: async (url, options = {}) => {
    const response = await fetch(url, {
      ...options,
      method: 'GET',
    });
    return handleResponse(response);
  },

  post: async (url, body, options = {}) => {
    const response = await fetch(url, {
      ...options,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      body: JSON.stringify(body),
    });
    return handleResponse(response);
  },

  put: async (url, body, options = {}) => {
    const response = await fetch(url, {
      ...options,
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      body: JSON.stringify(body),
    });
    return handleResponse(response);
  },

  patch: async (url, body, options = {}) => {
    const response = await fetch(url, {
      ...options,
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      body: body ? JSON.stringify(body) : undefined,
    });
    return handleResponse(response);
  },

  delete: async (url, options = {}) => {
    const response = await fetch(url, {
      ...options,
      method: 'DELETE',
    });
    return handleResponse(response);
  }
};
