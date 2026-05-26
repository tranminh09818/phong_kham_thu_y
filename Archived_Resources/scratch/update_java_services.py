import os

# 1. Modify GeminiService.java
gemini_path = r"d:\QLy Phòng Khám Thú Y\Backend\src\main\java\com\rexi\pkty\service\GeminiService.java"
if os.path.exists(gemini_path):
    with open(gemini_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Add imports if not present
    if "import org.springframework.beans.factory.annotation.Autowired;" not in content:
        content = content.replace(
            "import org.springframework.beans.factory.annotation.Value;",
            "import org.springframework.beans.factory.annotation.Value;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.jdbc.core.JdbcTemplate;"
        )

    # Add getModelName() method and jdbcTemplate injection
    target_class_decl = "public class GeminiService {"
    replacement_decl = """public class GeminiService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getModelName() {
        try {
            String dbModel = jdbcTemplate.queryForObject(
                "SELECT gia_tri FROM CauHinhHeThong WHERE ten_cau_hinh = 'gemini_model'", 
                String.class);
            if (dbModel != null && !dbModel.trim().isEmpty()) {
                return dbModel.trim();
            }
        } catch (Exception e) {
            // Fallback
        }
        return modelName;
    }"""
    if "private JdbcTemplate jdbcTemplate;" not in content:
        content = content.replace(target_class_decl, replacement_decl)

    # Replace modelName usage in URL
    content = content.replace(
        'models/" + modelName + ":generateContent?key="',
        'models/" + getModelName() + ":generateContent?key="'
    )

    with open(gemini_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("GeminiService.java modified successfully!")

# 2. Modify GroqService.java
groq_path = r"d:\QLy Phòng Khám Thú Y\Backend\src\main\java\com\rexi\pkty\service\GroqService.java"
if os.path.exists(groq_path):
    with open(groq_path, "r", encoding="utf-8") as f:
        content = f.read()

    if "import org.springframework.beans.factory.annotation.Autowired;" not in content:
        content = content.replace(
            "import org.springframework.beans.factory.annotation.Value;",
            "import org.springframework.beans.factory.annotation.Value;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.jdbc.core.JdbcTemplate;"
        )

    target_class_decl = "public class GroqService {"
    replacement_decl = """public class GroqService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getModelName() {
        try {
            String dbModel = jdbcTemplate.queryForObject(
                "SELECT gia_tri FROM CauHinhHeThong WHERE ten_cau_hinh = 'groq_model'", 
                String.class);
            if (dbModel != null && !dbModel.trim().isEmpty()) {
                return dbModel.trim();
            }
        } catch (Exception e) {
        }
        return modelName;
    }

    private String getVisionModelName() {
        try {
            String dbModel = jdbcTemplate.queryForObject(
                "SELECT gia_tri FROM CauHinhHeThong WHERE ten_cau_hinh = 'groq_vision_model'", 
                String.class);
            if (dbModel != null && !dbModel.trim().isEmpty()) {
                return dbModel.trim();
            }
        } catch (Exception e) {
        }
        return visionModelName;
    }"""
    if "private JdbcTemplate jdbcTemplate;" not in content:
        content = content.replace(target_class_decl, replacement_decl)

    content = content.replace(
        "String selectedModel = hasImage ? visionModelName : modelName;",
        "String selectedModel = hasImage ? getVisionModelName() : getModelName();"
    )

    with open(groq_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("GroqService.java modified successfully!")

# 3. Modify OpenRouterService.java
openrouter_path = r"d:\QLy Phòng Khám Thú Y\Backend\src\main\java\com\rexi\pkty\service\OpenRouterService.java"
if os.path.exists(openrouter_path):
    with open(openrouter_path, "r", encoding="utf-8") as f:
        content = f.read()

    if "import org.springframework.beans.factory.annotation.Autowired;" not in content:
        content = content.replace(
            "import org.springframework.beans.factory.annotation.Value;",
            "import org.springframework.beans.factory.annotation.Value;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.jdbc.core.JdbcTemplate;"
        )

    target_class_decl = "public class OpenRouterService {"
    replacement_decl = """public class OpenRouterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getModelName() {
        try {
            String dbModel = jdbcTemplate.queryForObject(
                "SELECT gia_tri FROM CauHinhHeThong WHERE ten_cau_hinh = 'openrouter_model'", 
                String.class);
            if (dbModel != null && !dbModel.trim().isEmpty()) {
                return dbModel.trim();
            }
        } catch (Exception e) {
        }
        return modelName;
    }"""
    if "private JdbcTemplate jdbcTemplate;" not in content:
        content = content.replace(target_class_decl, replacement_decl)

    content = content.replace(
        '"model", modelName,',
        '"model", getModelName(),'
    )

    with open(openrouter_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("OpenRouterService.java modified successfully!")
