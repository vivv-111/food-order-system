import { API_BASE } from "../config.js";
import { getAuthHeaders, parseJsonResponse } from "./http.js";

export async function fetchCart() {
  const res = await fetch(`${API_BASE}/cart`, {
    method: "GET",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}

export async function upsertCartItem(menuItemId, quantity) {
  const res = await fetch(`${API_BASE}/cart/items`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders()
    },
    body: JSON.stringify({ menuItemId, quantity })
  });

  return parseJsonResponse(res);
}

export async function removeCartItem(menuItemId) {
  const res = await fetch(`${API_BASE}/cart/items/${menuItemId}`, {
    method: "DELETE",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}
