const fs = require('fs');
const path = 'C:/Users/andre/.gemini/antigravity/brain/4ca6a4c5-54c7-4306-8db8-36d60d411881/.system_generated/logs/transcript_full.jsonl';
const lines = fs.readFileSync(path, 'utf8').split('\n');

const fileContents = {};

for (const line of lines) {
    if (!line) continue;
    const step = JSON.parse(line);
    if (step.tool_calls) {
        for (const tc of step.tool_calls) {
            const funcName = tc.name || (tc.function ? tc.function.name : null);
            let args = tc.args;
            if (!args && tc.function && typeof tc.function.arguments === 'string') {
                try { args = JSON.parse(tc.function.arguments); } catch(e) {}
            }
            if (!funcName || !args) continue;
            
            const normalizedFuncName = funcName.replace('default_api:', '');
            
            if (normalizedFuncName === 'write_to_file') {
                fileContents[args.TargetFile] = args.CodeContent;
            } else if (normalizedFuncName === 'multi_replace_file_content' || normalizedFuncName === 'replace_file_content') {
                if (!fileContents[args.TargetFile]) {
                    try {
                        fileContents[args.TargetFile] = fs.readFileSync(args.TargetFile, 'utf8'); // fallback
                    } catch(e) {}
                }
                let content = fileContents[args.TargetFile];
                if (content) {
                    if (normalizedFuncName === 'replace_file_content') {
                        content = content.replace(args.TargetContent, args.ReplacementContent);
                    } else if (normalizedFuncName === 'multi_replace_file_content' && args.ReplacementChunks) {
                        for (const chunk of args.ReplacementChunks) {
                            content = content.replace(chunk.TargetContent, chunk.ReplacementContent);
                        }
                    }
                    fileContents[args.TargetFile] = content;
                }
            }
        }
    }
}

const filesToRestore = [
    'c:\\Users\\andre\\IdeaProjects\\academic-info-2.0\\backend\\build.gradle',
    'c:\\Users\\andre\\IdeaProjects\\academic-info-2.0\\backend\\src\\main\\resources\\application.yaml',
    'c:\\Users\\andre\\IdeaProjects\\academic-info-2.0\\frontend\\src\\app\\app.config.ts',
    'c:\\Users\\andre\\IdeaProjects\\academic-info-2.0\\frontend\\src\\app\\app.routes.ts'
];

for (const [targetFile, content] of Object.entries(fileContents)) {
    const match = filesToRestore.find(f => f.toLowerCase() === targetFile.toLowerCase());
    if (match) {
        fs.writeFileSync(match, content);
        console.log('Restored: ' + match);
    }
}
