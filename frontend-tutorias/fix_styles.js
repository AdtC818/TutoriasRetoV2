const fs = require('fs');

// 1. Replace Register.jsx class names
let registerJsx = fs.readFileSync('src/pages/Register.jsx', 'utf8');
registerJsx = registerJsx.replace(/className="login-/g, 'className="register-');
fs.writeFileSync('src/pages/Register.jsx', registerJsx, 'utf8');

// 2. Build Register.css based on Login.css to restore the exact original visuals
let loginCss = fs.readFileSync('src/pages/Login.css', 'utf8');
let registerCss = loginCss.replace(/\.login-/g, '.register-');

// Add the custom select style from before
registerCss += `
select.register-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23333' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;
}
`;

fs.writeFileSync('src/pages/Register.css', registerCss, 'utf8');
console.log('Fixed styles to avoid collision');
