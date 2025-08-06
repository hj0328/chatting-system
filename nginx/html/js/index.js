document.addEventListener("DOMContentLoaded", () => {
    const signupSection = document.getElementById("signupSection");
    const loginSection = document.getElementById("loginSection");

    const baseURL = "https://localhost";

    document.getElementById("loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("loginUsername").value;
        const password = document.getElementById("loginPassword").value;

        const res = await fetch(`${baseURL}/api/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("currentUser", JSON.stringify(data));
            window.location.href = "/chat.html";
        } else {
            alert("로그인 실패");
        }
    });

    document.getElementById("signupForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("signupUsername").value;
        const password = document.getElementById("signupPassword").value;

        const res = await fetch(`${baseURL}/api/signup`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem("accessToken", data.accessToken);
            alert("회원가입 성공! 로그인해주세요.");
        } else {
            alert("회원가입 실패");
        }
    });
});
