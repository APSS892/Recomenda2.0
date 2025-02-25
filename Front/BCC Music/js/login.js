const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const nome = document.getElementById("username").value;
    const senha = document.getElementById("password").value;

    try {
        const params = new URLSearchParams();
        params.append("nome", nome);
        params.append("senha", senha);

        const response = await axios.post("http://localhost:8080/usuarios/login", params, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });

        if (response.status === 200) {
            const userId = response.data; // Pega o ID retornado pelo backend
            localStorage.setItem("userId", userId); // Salva o ID no localStorage

            // Redireciona para a página de artistas
            window.location.href = "artistas.html";
        }
    } catch (error) {
        if (error.response) {
            alert("Erro: " + error.response.data);
        } else {
            alert("Erro ao conectar ao servidor.");
        }
    }
});
