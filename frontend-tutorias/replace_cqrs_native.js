const fs = require('fs');
const path = require('path');

function walkDir(dir, callback) {
  fs.readdirSync(dir).forEach(f => {
    let dirPath = path.join(dir, f);
    let isDirectory = fs.statSync(dirPath).isDirectory();
    isDirectory ? walkDir(dirPath, callback) : callback(path.join(dir, f));
  });
}

walkDir('src', function(filePath) {
  if (filePath.endsWith('.js') || filePath.endsWith('.jsx')) {
    let content = fs.readFileSync(filePath, 'utf8');
    let original = content;

    content = content.replace(/import\s+\{\s*apiClient\s*\}\s+from\s+['\"](.*?)axiosConfig['\"];?/g, 
        ""import { commandClient } from '"" + ""$"" + ""1commands';\nimport { queryClient } from '"" + ""$"" + ""1queries';"");

    content = content.replace(/apiClient\.get/g, 'queryClient.get');
    content = content.replace(/apiClient\.post/g, 'commandClient.post');
    content = content.replace(/apiClient\.put/g, 'commandClient.put');
    content = content.replace(/apiClient\.delete/g, 'commandClient.delete');
    content = content.replace(/apiClient\.patch/g, 'commandClient.patch');

    if (content !== original) {
      fs.writeFileSync(filePath, content, 'utf8');
      console.log('Updated: ' + filePath);
    }
  }
});
