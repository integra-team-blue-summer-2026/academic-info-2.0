import sys, re, os

def remove_comments(text, ext):
    if ext in ['.java', '.ts', '.css']:
        pattern = r'(".*?"|\'.*?\'|.*?|\"\"\"[\s\S]*?\"\"\")|(/\*[\s\S]*?\*/|//[^\r\n]*)'
        return re.sub(pattern, lambda m: m.group(1) if m.group(1) else '', text)
    elif ext == '.html':
        return re.sub(r'<!--[\s\S]*?-->', '', text)
    elif ext == '.yaml':
        return re.sub(r'#[^\r\n]*', '', text)
    return text

files = [
    "backend/build.gradle",
    "backend/src/main/resources/application.yaml",
    "backend/src/main/java/cloudflight/integra/backend/auth/model/AppUser.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/model/LoginRequestDto.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/model/LoginResponse.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/model/RegisterRequestDto.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/AppUserRepository.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/AppUserDetailsService.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/AuthService.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/AuthController.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/JwtService.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/JwtAuthFilter.java",
    "backend/src/main/java/cloudflight/integra/backend/auth/SecurityConfig.java",
    "backend/src/test/java/cloudflight/integra/backend/auth/JwtServiceTest.java",
    "backend/src/test/java/cloudflight/integra/backend/auth/AuthControllerTest.java",
    "backend/src/test/java/cloudflight/integra/backend/course/CourseControllerTest.java",
    "backend/src/test/java/cloudflight/integra/backend/studentexam/StudentExamControllerTest.java",
    "backend/src/test/java/cloudflight/integra/backend/teacher/TeacherControllerTest.java",
    "frontend/src/app/app.config.ts",
    "frontend/src/app/app.routes.ts",
    "frontend/src/app/core/guards/auth.guard.ts",
    "frontend/src/app/core/interceptors/auth.interceptor.ts",
    "frontend/src/app/core/services/auth.service.ts",
    "frontend/src/app/features/auth/login/login.component.ts",
    "frontend/src/app/features/auth/login/login.component.html",
    "frontend/src/app/features/auth/login/login.component.css",
    "frontend/src/app/features/auth/signup/signup.component.ts",
    "frontend/src/app/features/auth/signup/signup.component.html",
    "frontend/src/app/features/auth/signup/signup.component.css"
]

base_dir = r"C:\Users\andre\IdeaProjects\academic-info-2.0"
for f in files:
    path = os.path.join(base_dir, f)
    if not os.path.exists(path):
        print(f"File not found: {f}")
        continue
    with open(path, 'r', encoding='utf-8') as file:
        content = file.read()
    
    ext = os.path.splitext(path)[1]
    if ext == '': ext = '.java' if 'gradle' not in f else '.gradle'
    if ext == '.gradle': ext = '.java'

    new_content = remove_comments(content, ext)
    
    lines = [line for line in new_content.splitlines() if line.strip() != '']
    new_content = '\n'.join(lines) + '\n'

    with open(path, 'w', encoding='utf-8') as file:
        file.write(new_content)
    print(f"Processed {f}")

