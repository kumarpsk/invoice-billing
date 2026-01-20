import { getToken } from './auth';

const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const res = await fetch(`${baseUrl}${path}`, { ...options, headers });
  const json = await res.json();
  if (!res.ok || json.success === false) {
    throw new Error(json.message || 'Request failed');
  }
  return json.data as T;
}
