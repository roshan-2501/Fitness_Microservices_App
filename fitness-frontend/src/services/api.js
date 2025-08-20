import axios from "axios";

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL:API_URL
});

// Add a request interceptor to include user ID and token in headers
api.interceptors.request.use((config) => {
    // Assuming userId and token are stored in localStorage
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');

    // If token exists, add it to the Authorization header
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    // If userId exists, add it to the X-User-ID header
    if (userId) {
        config.headers['X-User-ID'] = userId;
    }
    return config;
}
);


export const getActivities = () => api.get('/activities');
export const addActivity = (activity) => api.post('/activities', activity);
export const getActivityDetail = (id) => api.get(`/recommendations/activity/${id}`);