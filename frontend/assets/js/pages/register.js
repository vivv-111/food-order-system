import { register } from "../api/auth.js";

const form = document.getElementById("register-form");
const messageEl = document.getElementById("message");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const formData = new FormData(form);
  const payload = {
    userId: formData.get("userId"),
    userName: formData.get("userName"),
    email: formData.get("email"),
    password: formData.get("password")
  };

  try {
    const data = await register(payload);
    localStorage.setItem("accessToken", data.token);
    localStorage.setItem("userId", data.userId);
    localStorage.setItem("role", data.role);
    messageEl.textContent = "Register successful, redirecting...";
    window.location.href = "menu.html";
  } catch (err) {
    messageEl.textContent = err.message;
  }
});
