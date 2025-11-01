/**
 * Build query string from params object
 */
export function buildQueryString(params: Record<string, unknown>): string {
  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      queryParams.append(key, String(value));
    }
  });

  const queryString = queryParams.toString();
  return queryString ? `?${queryString}` : '';
}

/**
 * Parse query string to object
 */
export function parseQueryString(queryString: string): Record<string, string> {
  const params = new URLSearchParams(queryString);
  const result: Record<string, string> = {};

  params.forEach((value, key) => {
    result[key] = value;
  });

  return result;
}

/**
 * Update URL with new query params without reload
 */
export function updateUrlParams(params: Record<string, unknown>): void {
  const queryString = buildQueryString(params);
  const newUrl = `${window.location.pathname}${queryString}`;
  window.history.pushState({}, '', newUrl);
}

/**
 * Get query param from current URL
 */
export function getQueryParam(key: string): string | null {
  const params = new URLSearchParams(window.location.search);
  return params.get(key);
}

/**
 * Remove empty params from object
 */
export function removeEmptyParams<T extends Record<string, unknown>>(params: T): Partial<T> {
  const result: Partial<T> = {};

  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      result[key as keyof T] = value as T[keyof T];
    }
  });

  return result;
}

