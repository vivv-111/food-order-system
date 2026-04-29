import { fetchCart, removeCartItem, upsertCartItem } from "../api/cart.js";
import { submitOrder } from "../api/orders.js";

const messageEl = document.getElementById("message");
const listEl = document.getElementById("cart-list");
const totalEl = document.getElementById("cart-total");
const userLabelEl = document.getElementById("user-label");

const token = localStorage.getItem("accessToken");
const userId = localStorage.getItem("userId") || "";
const role = localStorage.getItem("role") || "USER";

if (!token) {
  window.location.href = "login.html";
}

userLabelEl.textContent = `${userId} (${role})`;

function showMessage(text, isError = false) {
  messageEl.textContent = text;
  messageEl.className = isError ? "error" : "success";
}

function renderCart(cart) {
  const items = cart.items || [];
  if (!items.length) {
    listEl.innerHTML = "<p>Your cart is empty.</p>";
    totalEl.textContent = "$0.00";
    return;
  }

  listEl.innerHTML = `
    <table class="table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Qty</th>
          <th>Price</th>
          <th>Line Total</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        ${items.map((item) => `
          <tr>
            <td>${item.name}</td>
            <td>
              <button data-dec-id="${item.menuItemId}">-</button>
              <span class="qty-value">${item.quantity}</span>
              <button data-inc-id="${item.menuItemId}">+</button>
            </td>
            <td>$${Number(item.unitPrice).toFixed(2)}</td>
            <td>$${Number(item.lineTotal).toFixed(2)}</td>
            <td><button data-remove-id="${item.menuItemId}">Remove</button></td>
          </tr>
        `).join("")}
      </tbody>
    </table>
  `;

  totalEl.textContent = `$${Number(cart.totalAmount || 0).toFixed(2)}`;
}

async function loadCart() {
  try {
    const cart = await fetchCart();
    renderCart(cart);
  } catch (err) {
    showMessage(err.message, true);
  }
}

listEl.addEventListener("click", async (e) => {
  const target = e.target;
  if (!(target instanceof HTMLButtonElement)) {
    return;
  }

  const decId = Number(target.dataset.decId || 0);
  const incId = Number(target.dataset.incId || 0);
  const removeId = Number(target.dataset.removeId || 0);

  try {
    if (removeId) {
      await removeCartItem(removeId);
      showMessage("Item removed");
      await loadCart();
      return;
    }

    const row = target.closest("tr");
    const qtyEl = row?.querySelector(".qty-value");
    const currentQty = Number(qtyEl?.textContent || 0);

    if (incId) {
      await upsertCartItem(incId, currentQty + 1);
      await loadCart();
      return;
    }

    if (decId) {
      const nextQty = Math.max(0, currentQty - 1);
      if (nextQty === 0) {
        await removeCartItem(decId);
      } else {
        await upsertCartItem(decId, nextQty);
      }
      await loadCart();
    }
  } catch (err) {
    showMessage(err.message, true);
  }
});

document.getElementById("logout-btn").addEventListener("click", () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
  window.location.href = "login.html";
});

document.getElementById("submit-order-btn").addEventListener("click", async () => {
  try {
    const order = await submitOrder();
    showMessage(`Order #${order.orderId} submitted successfully.`);
    await loadCart();
  } catch (err) {
    showMessage(err.message, true);
  }
});

loadCart();
