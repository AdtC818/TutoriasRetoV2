const fs = require('fs');
const path = require('path');

const cwd = path.join('C:', 'Software2C', 'Tutorias', 'Tutorias', 'frontend-tutorias');

// 1. Read Login.css
const loginCssPath = path.join(cwd, 'src', 'pages', 'Login.css');
let loginCss = fs.readFileSync(loginCssPath, 'utf8');

// 2. Replace .login- with .register-
let registerCss = loginCss.replace(/\.login-/g, '.register-');

// 3. Add select styles
registerCss += `
select.register-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23333' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;
}
`;

// 4. Overwrite Register.css
const registerCssPath = path.join(cwd, 'src', 'pages', 'Register.css');
fs.writeFileSync(registerCssPath, registerCss, 'utf8');

// 5. Update Register.jsx
const registerJsxPath = path.join(cwd, 'src', 'pages', 'Register.jsx');
let registerJsx = fs.readFileSync(registerJsxPath, 'utf8');
registerJsx = registerJsx.replace(/className="login-/g, 'className="register-');
fs.writeFileSync(registerJsxPath, registerJsx, 'utf8');

console.log("Completado");
