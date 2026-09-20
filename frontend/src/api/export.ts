import request from './index'
import type { PersonalStatsRequest, QueryRequest } from './analyzer'

export type ExportType = 'user-hourly' | 'user-models'
export type PersonalExportType = 'models' | 'hourly'
export type ExportRequest = QueryRequest

export const exportData = (params: ExportRequest, exportType?: ExportType) => {
  return request.post<Blob>('/export/data', params, {
    responseType: 'blob',
    params: exportType ? { exportType } : undefined
  })
}

export const exportPersonalStats = (params: PersonalStatsRequest, exportType: PersonalExportType) => {
  return request.post<Blob>('/analyzer/personal/export', params, {
    responseType: 'blob',
    params: { exportType }
  })
}
