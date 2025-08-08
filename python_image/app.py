from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import torch
from diffusers import StableDiffusionPipeline
import base64
from io import BytesIO
import logging
from typing import Optional
import os

# Настройка логирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Stable Diffusion API", version="1.0.0")

# Глобальная переменная для пайплайна
pipeline: Optional[StableDiffusionPipeline] = None

class GenerateRequest(BaseModel):
    prompt: str
    width: int = 512
    height: int = 512
    num_inference_steps: int = 20
    guidance_scale: float = 7.5

@app.on_event("startup")
async def load_model():
    """Загружаем модель при старте"""
    global pipeline
    try:
        logger.info("🚀 Загружаем Stable Diffusion модель...")
        
        # Проверяем доступность устройства
        if torch.cuda.is_available():
            device = "cuda"
            torch_dtype = torch.float16
            logger.info("✅ Используем CUDA")
        else:
            device = "cpu"
            torch_dtype = torch.float32
            logger.info("⚠️ Используем CPU (будет медленно)")
        
        # Путь к модели из переменной окружения или по умолчанию
        model_id = os.getenv("HF_MODEL_REPO", "runwayml/stable-diffusion-v1-5")
        model_path = os.getenv("MODEL_PATH", None)
        
        logger.info(f"📂 Модель: {model_path if model_path else model_id}")
        
        # Загружаем модель
        if model_path and os.path.exists(model_path):
            # Если есть локальная модель
            pipeline = StableDiffusionPipeline.from_pretrained(
                model_path,
                torch_dtype=torch_dtype,
                use_safetensors=True,
                local_files_only=True
            )
            logger.info("📁 Загружена локальная модель")
        else:
            # Загружаем из HuggingFace
            pipeline = StableDiffusionPipeline.from_pretrained(
                model_id,
                torch_dtype=torch_dtype,
                use_safetensors=True
            )
            logger.info("🌐 Загружена модель из HuggingFace")
        
        # Перемещаем на устройство
        pipeline = pipeline.to(device)
        
        # Оптимизации для экономии памяти
        if device == "cuda":
            pipeline.enable_attention_slicing()
            try:
                pipeline.enable_xformers_memory_efficient_attention()
                logger.info("✨ Включена оптимизация xformers")
            except Exception as e:
                logger.warning(f"⚠️ xformers недоступен: {e}")
        else:
            # Оптимизации для CPU
            pipeline.enable_attention_slicing()
        
        logger.info("🎉 Модель успешно загружена и готова к работе!")
        
        # Тестовая генерация для прогрева
        logger.info("🔥 Прогреваем модель...")
        with torch.no_grad():
            _ = pipeline("test", num_inference_steps=1, guidance_scale=1.0, width=64, height=64)
        logger.info("✅ Модель прогрета!")
        
    except Exception as e:
        logger.error(f"❌ Ошибка загрузки модели: {e}")
        raise e

@app.get("/health")
async def health_check():
    """Проверка готовности сервиса"""
    if pipeline is None:
        raise HTTPException(status_code=503, detail="Модель еще загружается")
    
    return {
        "status": "healthy",
        "model_loaded": True,
        "device": "cuda" if torch.cuda.is_available() else "cpu",
        "model": os.getenv("HF_MODEL_REPO", "runwayml/stable-diffusion-v1-5"),
        "torch_version": torch.__version__
    }

@app.post("/generate")
async def generate_image(request: GenerateRequest):
    """Генерация изображения - основной эндпоинт для Java приложения"""
    if pipeline is None:
        raise HTTPException(status_code=503, detail="Модель не загружена")
    
    try:
        logger.info(f"🎨 Генерируем: '{request.prompt}' ({request.width}x{request.height})")
        
        # Валидация параметров
        if request.width > 1024 or request.height > 1024:
            raise HTTPException(status_code=400, detail="Максимальное разрешение 1024x1024")
        
        if request.num_inference_steps > 50:
            raise HTTPException(status_code=400, detail="Максимум 50 шагов")
        
        # Генерируем изображение
        device = next(pipeline.unet.parameters()).device
        
        with torch.autocast(device.type if device.type != "cpu" else "cpu"):
            result = pipeline(
                prompt=request.prompt,
                width=request.width,
                height=request.height,
                num_inference_steps=request.num_inference_steps,
                guidance_scale=request.guidance_scale,
                generator=torch.Generator(device=device).manual_seed(42)
            )
        
        image = result.images[0]
        
        # Конвертируем в base64
        buffer = BytesIO()
        image.save(buffer, format="PNG", optimize=True)
        image_base64 = base64.b64encode(buffer.getvalue()).decode()
        
        logger.info(f"✅ Изображение готово! Размер: {len(image_base64)} символов")
        
        return {
            "image": image_base64,
            "prompt": request.prompt,
            "width": request.width,
            "height": request.height,
            "steps": request.num_inference_steps,
            "guidance": request.guidance_scale
        }
        
    except Exception as e:
        logger.error(f"❌ Ошибка генерации: {e}")
        raise HTTPException(status_code=500, detail=f"Ошибка генерации: {str(e)}")

@app.get("/")
async def root():
    """Информация об API"""
    return {
        "service": "Stable Diffusion API",
        "version": "1.0.0",
        "model": os.getenv("HF_MODEL_REPO", "runwayml/stable-diffusion-v1-5"),
        "endpoints": {
            "health": "GET /health - Проверка готовности",
            "generate": "POST /generate - Генерация изображения",
            "docs": "GET /docs - Swagger документация"
        },
        "status": "ready" if pipeline else "loading"
    }

@app.get("/status")
async def get_status():
    """Детальный статус сервиса"""
    return {
        "model_loaded": pipeline is not None,
        "cuda_available": torch.cuda.is_available(),
        "device": "cuda" if torch.cuda.is_available() else "cpu",
        "torch_version": torch.__version__,
        "model_path": os.getenv("MODEL_PATH", "HuggingFace"),
        "model_repo": os.getenv("HF_MODEL_REPO", "runwayml/stable-diffusion-v1-5")
    }

if __name__ == "__main__":
    import uvicorn
    logger.info("🚀 Запускаем Stable Diffusion API...")
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")
