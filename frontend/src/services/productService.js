import { apiClient } from '../api/client';

export const productService = {
  getFilters: async (options = {}) => {
    return apiClient.get('/api/products/filters', options);
  },

  getProducts: async (params, options = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const url = `/api/products${queryString ? `?${queryString}` : ''}`;
    return apiClient.get(url, options);
  },

  getProduct: async (productCode, options = {}) => {
    return apiClient.get(`/api/products/${productCode}`, options);
  },

  getMetadata: async (options = {}) => {
    return apiClient.get('/api/products/metadata', options);
  },

  createProduct: async (productData, options = {}) => {
    return apiClient.post('/api/products', productData, options);
  },

  updateProduct: async (productCode, productData, options = {}) => {
    return apiClient.put(`/api/products/${productCode}`, productData, options);
  },

  deactivateProduct: async (productCode, options = {}) => {
    return apiClient.patch(`/api/products/${productCode}/deactivate`, null, options);
  },

  activateProduct: async (productCode, options = {}) => {
    return apiClient.patch(`/api/products/${productCode}/activate`, null, options);
  }
};
