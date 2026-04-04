function AuthLayout({ children }) {

  return (
    <div style={{ display: "flex", height: "100vh" }}>

      <div
        style={{
          flex: 1,
          background: "#8C2F39",
          color: "white",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          flexDirection: "column"
        }}
      >
        <h1>PrintIT</h1>
        <p>Print smarter. Order faster.</p>
      </div>

      <div
        style={{
          flex: 1,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          background: "#F4F4F4"
        }}
      >
        {children}
      </div>

    </div>
  );
}

export default AuthLayout;