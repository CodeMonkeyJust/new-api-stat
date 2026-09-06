import request from './index'

export interface LoginRequest {
  username: string
  password: string
}

export interface UserDTO {
  id: number
  username: string
  email: string
  displayName: string
  role: number
  status: number
  group: string
}

export interface LoginResponse {
  success: boolean
  message: string
  user: UserDTO | null
}

export const getCsrfToken = () => {
  return request.get('/auth/csrf')
}

export const login = (data: LoginRequest) => {
  return request.post<any, LoginResponse>('/auth/login', data)
}

export const logout = () => {
  return request.post('/auth/logout')
}

export const getCurrentUser = () => {
  return request.get<any, UserDTO>('/auth/current')
}
