import "../pages/Auth.css";

function AuthLayout({children}){

return(

<div className="auth-container">

<div className="auth-left">

<div className="brand">

<h1>PrintIT</h1>

<h3>Print smarter. Order faster.</h3>

<p>
Upload files, order prints, and pick them up anytime on campus.
</p>

</div>

</div>

<div className="auth-right">

{children}

</div>

</div>

);

}

export default AuthLayout;