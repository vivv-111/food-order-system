import { fetchMenu } from "../api/menu.js";
import { upsertCartItem } from "../api/cart.js";

const messageEl = document.getElementById("message");
const listEl = document.getElementById("menu-list");
const userLabelEl = document.getElementById("user-label");
const typeFilterEl = document.getElementById("type-filter");
const adminLinkEl = document.getElementById("admin-link");

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
if (role !== "ADMIN") {
  adminLinkEl.style.display = "none";
}

function showMessage(text, isError = false) {
  messageEl.textContent = text;
  messageEl.className = isError ? "error" : "success";
}

function renderMenu(items) {
  if (!items.length) {
    listEl.innerHTML = "<p>No menu items found.</p>";
    return;
  }

  listEl.innerHTML = items.map((item) => {
    return `
      <article class="card menu-item">
        <div class="menu-header">
          <h3>${item.name}</h3>
          <span class="price">$${Number(item.price).toFixed(2)}</span>
        </div>
        <p><b>Type:</b> ${item.type}</p>
        <p><b>Ingredients:</b> ${item.ingredients || "-"}</p>
        <p>${item.description || ""}</p>
        <div class="row controls">
          <input type="number" min="1" value="1" id="qty-${item.id}" class="qty-input" />
          <button data-add-id="${item.id}">Add To Cart</button>
        </div>
      </article>
    `;
  }).join("");
}

async function loadMenu() {
  const type = typeFilterEl.value;
  try {
    const items = await fetchMenu(type);
    renderMenu(items);
  } catch (err) {
    showMessage(err.message, true);
  }
}

listEl.addEventListener("click", async (e) => {
  const target = e.target;
  if (!(target instanceof HTMLButtonElement)) {
    return;
  }

  const menuItemId = Number(target.dataset.addId || 0);
  if (!menuItemId) {
    return;
  }

  const qtyInput = document.getElementById(`qty-${menuItemId}`);
  const quantity = Number(qtyInput?.value || 1);

  if (!Number.isInteger(quantity) || quantity <= 0) {
    showMessage("Quantity must be at least 1", true);
    return;
  }

  try {
    await upsertCartItem(menuItemId, quantity);
    showMessage("Added to cart successfully");
  } catch (err) {
    showMessage(err.message, true);
  }
});

typeFilterEl.addEventListener("change", loadMenu);

document.getElementById("logout-btn").addEventListener("click", () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
  window.location.href = "login.html";
});

loadMenu();