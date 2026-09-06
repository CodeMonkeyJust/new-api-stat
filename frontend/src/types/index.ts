
export interface QueryParams {
  startDate: string
  endDate: string
  dimension: string
  rankType: string
  topN: number
}

export interface SummaryData {
  totalCount: number
  totalTokens: number
  totalCost: number
  avgTime: number
}

export interface RankData {
  name: string
  value: number
  cost: number
  count: number
}

export interface HourlyData {
  hour: number
  quota: number
  cost: number
  count: number
  users: number
}

export interface DailyData {
  date: string
  quota: number
  cost: number
  count: number
  promptTokens: number
  completionTokens: number
}
