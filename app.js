var port = process.env.PORT || 8000;

server.listen(port, function() {
    console.log("App is running on port " + port);
});

const port = process.env.PORT || 8000;

server.listen(port, () => {
    console.log("App is running on port " + port);
});