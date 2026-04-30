import { API_BASE } from "../config.js";
import { getAuthHeaders, parseJsonResponse } from "./http.js";

export async function fetchAdminOrders() {
  const res = await fetch(`${API_BASE}/admin/orders`, {
    method: "GET",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}

export async function deleteAdminOrder(orderId) {
  const res = await fetch(`${API_BASE}/admin/orders/${orderId}`, {
    method: "DELETE",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}
