/**
 * Centralized API Client
 * Handles fetching, error normalization, and response unwrapping.
 */

const normalizeError = (errorData, status) => {
  // New standard error format
  if (errorData?.error && typeof errorData.error === 'object') {
    const errObj = errorData.error;
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
  const contentType = response.headers.get('content-type');
  
  if (response.ok && contentType && contentType.includes('application/pdf')) {
    const blob = await response.blob().catch(() => new Blob());
    return {
      data: blob,
      headers: response.headers,
      isWrapped: false
    };
  }

  let responseData;
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
    // Do not trigger global logout for authentication endpoint itself
    if (response.status === 401 && !response.url.includes('/api/auth/login')) {
      window.dispatchEvent(new CustomEvent('auth-error'));
    }
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
    let finalUrl = url;
    if (options.params) {
      // Filter out null or undefined values
      const cleanParams = Object.fromEntries(
        Object.entries(options.params).filter(([_, v]) => v != null)
      );
      const queryString = new URLSearchParams(cleanParams).toString();
      if (queryString) {
        finalUrl += (url.includes('?') ? '&' : '?') + queryString;
      }
      // Remove params from options so it's not passed to fetch
      const { params, ...fetchOptions } = options;
      options = fetchOptions;
    }
    
    const response = await fetch(finalUrl, {
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
