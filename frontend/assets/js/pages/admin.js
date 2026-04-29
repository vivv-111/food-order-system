import { createMenuItem } from "../api/menu.js";
import { deleteAdminOrder, fetchAdminOrders } from "../api/admin.js";

const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role") || "USER";
const userId = localStorage.getItem("userId") || "";

if (!token) {
  window.location.href = "login.html";
}

if (role !== "ADMIN") {
  alert("Admin only page");
  window.location.href = "menu.html";
}

const userLabelEl = document.getElementById("user-label");
const messageEl = document.getElementById("message");
const adminOrdersEl = document.getElementById("admin-orders");
const formEl = document.getElementById("add-menu-form");

userLabelEl.textContent = `${userId} (${role})`;

function showMessage(text, isError = false) {
  messageEl.textContent = text;
  messageEl.className = isError ? "error" : "success";
}

function renderOrders(orders) {
  if (!orders.length) {
    adminOrdersEl.innerHTML = "<p>No orders yet.</p>";
    return;
  }

  adminOrdersEl.innerHTML = orders.map((order) => {
    const rows = order.items.map((item) => `
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
          <div class="order-actions">
            <span class="price">$${Number(order.totalAmount).toFixed(2)}</span>
            <button type="button" class="danger-btn" data-delete-order-id="${order.orderId}">Delete Order</button>
          </div>
        </div>
        <p><b>User:</b> ${order.userId} / ${order.userName}</p>
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
          <tbody>${rows}</tbody>
        </table>
      </article>
    `;
  }).join("");
}

adminOrdersEl.addEventListener("click", async (e) => {
  const target = e.target;
  if (!(target instanceof HTMLButtonElement)) {
    return;
  }

  const orderId = Number(target.dataset.deleteOrderId || 0);
  if (!orderId) {
    return;
  }

  const confirmed = window.confirm(`Delete order #${orderId}? This action cannot be undone.`);
  if (!confirmed) {
    return;
  }

  try {
    await deleteAdminOrder(orderId);
    showMessage(`Order #${orderId} deleted.`);
    await refreshOrders();
  } catch (err) {
    showMessage(err.message, true);
  }
});

async function refreshOrders() {
  try {
    const orders = await fetchAdminOrders();
    renderOrders(orders);
  } catch (err) {
    showMessage(err.message, true);
  }
}

formEl.addEventListener("submit", async (e) => {
  e.preventDefault();
  const formData = new FormData(formEl);

  const payload = {
    name: formData.get("name"),
    type: formData.get("type"),
    ingredients: formData.get("ingredients"),
    description: formData.get("description"),
    price: Number(formData.get("price")),
    imageUrl: formData.get("imageUrl")
  };

  try {
    const created = await createMenuItem(payload);
    showMessage(`Menu item #${created.id} created.`);
    formEl.reset();
  } catch (err) {
    showMessage(err.message, true);
  }
});

document.getElementById("refresh-orders-btn").addEventListener("click", refreshOrders);

document.getElementById("logout-btn").addEventListener("click", () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
  window.location.href = "login.html";
});

refreshOrders();
