🍎 Happy Food - Tu Asistente Nutricional Inteligente

    Happy Food es una aplicación de escritorio desarrollada en Java que automatiza la planificación de comidas saludables. El proyecto soluciona la falta de tiempo y la barrera del idioma al conectar con bases de datos internacionales de recetas, traduciendo el contenido y generando planes listos para usar.

🚀 Funcionalidades Clave

    Búsqueda Inteligente: Filtra recetas por ingredientes, calorías o tipo de dieta (vegetariana, sin gluten, etc.) conectando con la API de Spoonacular.
    
    Traducción Automática: Olvídate del inglés. Todas las instrucciones e ingredientes se traducen al castellano en tiempo real.
    
    Planificador Semanal: Organiza tus comidas para los 7 días de la semana con un solo clic.
    
    Exportación a PDF: Genera un informe profesional con el menú semanal, ideal para imprimir o guardar en el móvil.
    
    Interfaz Moderna: Diseñada con JavaFX para ofrecer una experiencia fluida y visual.

🛠️ Tecnologías utilizadas

    Lenguaje: Java 17+
    
    Interfaz Gráfica: JavaFX & Scene Builder
    
    Gestión de Datos: API de Spoonacular & GSON (para procesar JSON)
    
    Informes: OpenPDF (Librería para creación de documentos)
    
    Arquitectura: Patrón Modelo-Vista-Controlador (MVC)
    

  ⚙️ Instalación y Requisitos
    
```text
1. Clona el repositorio: 
   git clone [https://github.com/tu-usuario/happy-food.git](https://github.com/tu-usuario/happy-food.git)

2. Requisitos de software:
   - Tener instalado el JDK 17 o superior.
   - Usar un IDE compatible (IntelliJ IDEA recomendado).

3. Configuración de la API (Spoonacular):
   - Regístrate en Spoonacular para obtener una API Key gratuita.
   - Abre el archivo 'ApiController.java'.
   - Busca la variable 'API_KEY' y sustituye su valor por tu clave personal.

4. Ejecución:
   - Importa el proyecto como proyecto Maven/Gradle.
   - Ejecuta la clase principal para iniciar Happy Food.
