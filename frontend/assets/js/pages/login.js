import { login } from "../api/auth.js";

const form = document.getElementById("login-form");
const messageEl = document.getElementById("message");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const formData = new FormData(form);
  const payload = {
    userId: formData.get("userId"),
    password: formData.get("password")
  };

  try {
    const data = await login(payload);
    localStorage.setItem("accessToken", data.token);
    localStorage.setItem("userId", data.userId);
    localStorage.setItem("role", data.role);
    messageEl.textContent = "Login successful, redirecting...";
    

    if (data.role === "ADMIN") {
      window.location.href = "admin.html";
    } else {
      window.location.href = "menu.html";
    }
  } catch (err) {
    messageEl.textContent = err.message;
  }
});