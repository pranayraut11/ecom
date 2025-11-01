export * from './dateUtils';
export * from './statusUtils';
export * from './errorUtils';
export * from './helpers';
export * from './urlUtils';
import dayjs from 'dayjs';
import { DATE_FORMAT, DATE_FORMAT_SHORT } from '@/constants';

/**
 * Format a date string or Date object to a readable format
 * @param date - Date string or Date object
 * @param format - Format string (default: YYYY-MM-DD HH:mm:ss)
 */
export function formatDate(date: string | Date | null | undefined, format: string = DATE_FORMAT): string {
  if (!date) return '-';
  return dayjs(date).format(format);
}

/**
 * Format a date to short format (YYYY-MM-DD)
 */
export function formatDateShort(date: string | Date | null | undefined): string {
  return formatDate(date, DATE_FORMAT_SHORT);
}

/**
 * Format a duration in milliseconds to human-readable format
 * @param ms - Duration in milliseconds
 */
export function formatDuration(ms: number | null | undefined): string {
  if (!ms) return '-';

  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) return `${days}d ${hours % 24}h`;
  if (hours > 0) return `${hours}h ${minutes % 60}m`;
  if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
  return `${seconds}s`;
}

/**
 * Get relative time from now
 * @param date - Date string or Date object
 */
export function getRelativeTime(date: string | Date | null | undefined): string {
  if (!date) return '-';

  const now = dayjs();
  const target = dayjs(date);
  const diffInSeconds = now.diff(target, 'second');

  if (diffInSeconds < 60) return 'Just now';
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`;
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`;
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)} days ago`;

  return formatDate(date);
}

/**
 * Check if a date is today
 */
export function isToday(date: string | Date | null | undefined): boolean {
  if (!date) return false;
  return dayjs(date).isSame(dayjs(), 'day');
}

/**
 * Check if a date is within a range
 */
export function isDateInRange(
  date: string | Date,
  startDate: string | Date | null,
  endDate: string | Date | null
): boolean {
  const target = dayjs(date);
  const start = startDate ? dayjs(startDate) : null;
  const end = endDate ? dayjs(endDate) : null;

  if (start && end) {
    return target.isAfter(start) && target.isBefore(end);
  }
  if (start) return target.isAfter(start);
  if (end) return target.isBefore(end);

  return true;
}

