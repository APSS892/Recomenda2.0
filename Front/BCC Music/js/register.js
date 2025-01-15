const registerForm = document.getElementById("registerForm");


registerForm.addEventListener("submit", async (event) => {
    event.preventDefault(); 

    const nome = document.getElementById("username").value;
    const senha = document.getElementById("password").value;
    const genero = document.getElementById("genero").value;
    const dataNascimentoInput = document.getElementById("data").value;

   
    if (!dataNascimentoInput) {
        alert("Por favor, selecione uma data de nascimento.");
        return;
    }

    
    const dataNascimento = new Date(dataNascimentoInput).toISOString().split("T")[0];

    try {
        
        const params = new URLSearchParams();
        params.append("nome", nome);
        params.append("senha", senha);
        params.append("genero", genero); 
        params.append("dataNascimento", dataNascimento);

        
        const response = await axios.post("http://localhost:8080/usuarios/cadastrar", params, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });

        
        alert(response.data); 
        window.location.href = "login.html";
    } catch (error) {
     
        if (error.response) {
            alert("Erro: " + error.response.data); 
        } else {
            alert("Erro ao conectar ao servidor.");
        }
    }
});
