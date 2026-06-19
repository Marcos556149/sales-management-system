import { apiClient } from '../api/client';

export const userService = {
getUsers: async (options = {}) => {
return apiClient.get('/api/users', options);
},

getUser: async (userId, options = {}) => {
return apiClient.get(`/api/users/${userId}`, options);
},

createUser: async (userData, options = {}) => {
return apiClient.post('/api/users', userData, options);
},

updateUser: async (userId, userData, options = {}) => {
return apiClient.put(`/api/users/${userId}`, userData, options);
},

changeStatus: async (userId, statusData, options = {}) => {
return apiClient.patch(
`/api/users/${userId}/status`,
statusData,
options
);
},

getMetadata: async (options = {}) => {
return apiClient.get('/api/users/metadata', options);
}
};
