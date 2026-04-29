import { API_BASE } from "../config.js";
import { getAuthHeaders, parseJsonResponse } from "./http.js";

export async function fetchMenu(type = "") {
  const query = type ? `?type=${encodeURIComponent(type)}` : "";
  const res = await fetch(`${API_BASE}/menu${query}`, {
    method: "GET",
    headers: {
      ...getAuthHeaders()
    }
  });

  return parseJsonResponse(res);
}

export async function createMenuItem(payload) {
  const res = await fetch(`${API_BASE}/admin/menu`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders()
    },
    body: JSON.stringify(payload)
  });

  return parseJsonResponse(res);
}
