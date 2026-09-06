import request from './index'

export type QueryDimension = 'user' | 'model' | 'group'
export type QueryRankType = 'total_cost' | 'call_count' | 'daily' | 'hourly'

export interface QueryRequest {
  startDate: string
  endDate: string
  dimension: QueryDimension
  rankType: QueryRankType
  topN: number
  username?: string
  usernames?: string[]
}

export interface SummaryResponse {
  totalCount: number
  totalTokens: number
  promptTokens: number
  completionTokens: number
  totalCost: number
  avgTime: number
}

export interface RankItem {
  name: string
  promptTokens: number
  completionTokens: number
  value: number
  cost: number
  count: number
}

export type RankResponse = RankItem[]

export interface HourlyItem {
  hour: number
  quota: number
  promptTokens: number
  completionTokens: number
  cost: number
  count: number
  users: number
}

export type HourlyResponse = HourlyItem[]

export interface DailyItem {
  date: string
  quota: number
  cost: number
  count: number
  promptTokens: number
  completionTokens: number
}

export type DailyResponse = DailyItem[]

export interface ModelDailyItem {
  date: string
  model: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

export type ModelDailyResponse = ModelDailyItem[]

export interface HourlyUserItem {
  username: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

export type HourlyUserResponse = HourlyUserItem[]

export interface ModelUserItem {
  username: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

export type ModelUserResponse = ModelUserItem[]

export interface UserBalanceItem {
  id: number
  username: string
  displayName: string
  email: string
  quota: number
  usedQuota: number
  remainingBalance: number
  spentBalance: number
  requestCount: number
  status: number
  group: string
}

export type UserBalanceResponse = UserBalanceItem[]

export interface DashboardSummary {
  totalCount: number
  totalTokens: number
  promptTokens: number
  completionTokens: number
  totalCost: number
  avgTime: number
}

export interface DailyTopUser {
  date: string
  topCostUser: string
  topCostValue: number
  topPromptTokensUser: string
  topPromptTokensValue: number
  topCompletionTokensUser: string
  topCompletionTokensValue: number
}

export interface DashboardResponse {
  total: DashboardSummary
  today: DashboardSummary
  yesterday: DashboardSummary
  last7DaysTopUsers: DailyTopUser[]
}

export interface UserItem {
  id: number
  username: string
  displayName: string
}

export type UserResponse = UserItem[]

export interface UserDailyItem {
  username: string
  date: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

export type UserDailyResponse = UserDailyItem[]

export const getSummary = (params: QueryRequest) => {
  return request<SummaryResponse>({
    url: '/analyzer/summary',
    method: 'post',
    data: params
  })
}

export const getRank = (params: QueryRequest) => {
  return request<RankResponse>({
    url: '/analyzer/rank',
    method: 'post',
    data: params
  })
}

export const getHourly = (params: QueryRequest) => {
  return request<HourlyResponse>({
    url: '/analyzer/hourly',
    method: 'post',
    data: params
  })
}

export const getDaily = (params: QueryRequest) => {
  return request<DailyResponse>({
    url: '/analyzer/daily',
    method: 'post',
    data: params
  })
}

export const getModelDaily = (params: QueryRequest) => {
  return request<ModelDailyResponse>({
    url: '/analyzer/model-daily',
    method: 'post',
    data: params
  })
}

export const getHourlyUsers = (params: QueryRequest, hour: number) => {
  return request<HourlyUserResponse>({
    url: '/analyzer/hourly-users',
    method: 'post',
    data: params,
    params: { hour }
  })
}

export const getModelUsers = (params: QueryRequest, model: string) => {
  return request<ModelUserResponse>({
    url: '/analyzer/model-users',
    method: 'post',
    data: params,
    params: { model }
  })
}

export const getUserBalances = () => {
  return request<UserBalanceResponse>({
    url: '/analyzer/user-balances',
    method: 'get'
  })
}

export const getDashboard = () => {
  return request<DashboardResponse>({
    url: '/analyzer/dashboard',
    method: 'get'
  })
}

export const getUsers = () => {
  return request<UserResponse>({
    url: '/analyzer/users',
    method: 'get'
  })
}

export interface PersonalSummary {
  totalCount: number
  promptTokens: number
  completionTokens: number
  totalTokens: number
  totalCost: number
}

export interface PersonalModelItem {
  model: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

export interface PersonalStatsResponse {
  username: string
  displayName: string
  summary: PersonalSummary
  models: PersonalModelItem[]
}

export const getPersonalStats = (data: { startDate: string; endDate: string }) => {
  return request<PersonalStatsResponse>({
    url: '/analyzer/personal/stats',
    method: 'post',
    data
  })
}
export const getUserDaily = (params: QueryRequest) => {
  return request<UserDailyResponse>({
    url: '/analyzer/user-daily',
    method: 'post',
    data: params
  })
}
