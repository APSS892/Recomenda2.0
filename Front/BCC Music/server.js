const express = require("express");
const path = require("path");

const app = express();
const PORT = 3060;

// Middleware para servir arquivos estáticos
app.use(express.static(path.join(__dirname)));

// Rota principal
app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "login.html"));
});

// Iniciar o servidor
app.listen(PORT, () => {
    console.log(`Servidor rodando em http://localhost:${PORT}`);
});
