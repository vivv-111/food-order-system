import { fetchOrders } from "../api/orders.js";

const messageEl = document.getElementById("message");
const listEl = document.getElementById("orders-list");
const userLabelEl = document.getElementById("user-label");

const token = localStorage.getItem("accessToken");
const userId = localStorage.getItem("userId") || "";
const role = localStorage.getItem("role") || "USER";

if (!token) {
  window.location.href = "login.html";
}

if (role === "ADMIN") {
  window.location.href = "admin.html";
}

userLabelEl.textContent = `${userId} (${role})`;

function showMessage(text, isError = false) {
  messageEl.textContent = text;
  messageEl.className = isError ? "error" : "success";
}

function renderOrders(orders) {
  if (!orders.length) {
    listEl.innerHTML = "<p>No order history yet.</p>";
    return;
  }

  listEl.innerHTML = orders.map((order) => {
    const itemsHtml = order.items.map((item) => `
      <tr>
        <td>${item.itemName}</td>
        <td>${item.quantity}</td>
        <td>$${Number(item.unitPrice).toFixed(2)}</td>
        <td>$${Number(item.lineTotal).toFixed(2)}</td>
      </tr>
    `).join("");

    return `
      <article class="card order-item-card">
        <div class="menu-header">
          <h3>Order #${order.orderId}</h3>
          <span class="price">$${Number(order.totalAmount).toFixed(2)}</span>
        </div>
        <p><b>Status:</b> ${order.status}</p>
        <p><b>Created:</b> ${order.createdAt || "-"}</p>
        <table class="table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Qty</th>
              <th>Unit Price</th>
              <th>Line Total</th>
            </tr>
          </thead>
          <tbody>
            ${itemsHtml}
          </tbody>
        </table>
      </article>
    `;
  }).join("");
}

async function loadOrders() {
  try {
    const orders = await fetchOrders();
    renderOrders(orders);
  } catch (err) {
    showMessage(err.message, true);
  }
}

document.getElementById("logout-btn").addEventListener("click", () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
  window.location.href = "login.html";
});

loadOrders();