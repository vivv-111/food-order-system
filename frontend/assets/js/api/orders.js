import { API_BASE } from "../config.js";
import { getAuthHeaders, parseJsonResponse } from "./http.js";

export async function submitOrder() {
  const res = await fetch(`${API_BASE}/orders/submit`, {
    method: "POST",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}

export async function fetchOrders() {
  const res = await fetch(`${API_BASE}/orders`, {
    method: "GET",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}
