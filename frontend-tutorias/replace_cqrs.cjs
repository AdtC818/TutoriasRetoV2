const fs = require('fs');
const glob = require('glob');

const files = glob.sync('src/**/*.{js,jsx}', { absolute: true });
console.log("Files found:", files.length);

files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  if (content.includes('apiClient')) {
    content = content.replace(/import\s+\{\s*apiClient\s*\}\s+from\s+['"]([^'"]*)axiosConfig['"];?/, "import { commandClient } from '$1commands';\nimport { queryClient } from '$1queries';");
    content = content.replace(/apiClient\.get/g, 'queryClient.get');
    content = content.replace(/apiClient\.post/g, 'commandClient.post');
    content = content.replace(/apiClient\.put/g, 'commandClient.put');
    content = content.replace(/apiClient\.delete/g, 'commandClient.delete');
    content = content.replace(/apiClient\.patch/g, 'commandClient.patch');
    content = content.replace(/apiClient/g, 'commandClient'); // catch remaining 
    fs.writeFileSync(file, content, 'utf8');
    console.log('Updated: ' + file);
  }
});
