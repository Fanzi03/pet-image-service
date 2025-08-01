import asyncio
import aiohttp
import base64
import logging
from pathlib import Path
from typing import Optional

# Настройка логирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class StabilityController:
    def __init__(self, base_url: str = "http://localhost:8000"):
        self.base_url = base_url
        self.session: Optional[aiohttp.ClientSession] = None
    
    async def __aenter__(self):
        self.session = aiohttp.ClientSession()
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session:
            await self.session.close()
    
    async def health_check(self) -> bool:
        """Проверка готовности сервиса"""
        try:
            async with self.session.get(f"{self.base_url}/health") as response:
                if response.status == 200:
                    data = await response.json()
                    logger.info(f"Сервис готов: {data}")
                    return True
                return False
        except Exception as e:
            logger.error(f"Сервис недоступен: {e}")
            return False
    
    async def generate_image(self, prompt: str, width: int = 512, height: int = 512, 
                           steps: int = 20, guidance: float = 7.5) -> Optional[bytes]:
        """Генерация изображения"""
        try:
            payload = {
                "prompt": prompt,
                "width": width,
                "height": height,
                "num_inference_steps": steps,
                "guidance_scale": guidance
            }
            
            logger.info(f"Генерируем: {prompt}")
            
            async with self.session.post(f"{self.base_url}/generate", json=payload) as response:
                if response.status == 200:
                    data = await response.json()
                    # Декодируем base64 в байты
                    image_data = base64.b64decode(data["image"])
                    logger.info("Изображение получено")
                    return image_data
                else:
                    error_text = await response.text()
                    logger.error(f"Ошибка генерации: {response.status} - {error_text}")
                    return None
                    
        except Exception as e:
            logger.error(f"Ошибка при генерации: {e}")
            return None
    
    async def save_image(self, image_data: bytes, filename: str) -> bool:
        """Сохранение изображения в файл"""
        try:
            output_dir = Path("output")
            output_dir.mkdir(exist_ok=True)
            
            filepath = output_dir / filename
            with open(filepath, 'wb') as f:
                f.write(image_data)
            
            logger.info(f"Изображение сохранено: {filepath}")
            return True
            
        except Exception as e:
            logger.error(f"Ошибка сохранения: {e}")
            return False

async def main():
    """Пример использования контроллера"""
    
    # Тестовые промпты
    prompts = [
        "beautiful sunset over mountains",
        "cute cat sitting in a garden",
        "futuristic city with flying cars"
    ]
    
    async with StabilityController() as controller:
        # Проверяем готовность сервиса
        logger.info("Проверяем сервис...")
        
        # Ждем пока сервис будет готов (макс 2 минуты)
        for attempt in range(24):  # 24 попытки по 5 секунд = 2 минуты
            if await controller.health_check():
                break
            logger.info(f"Попытка {attempt + 1}/24, ждем...")
            await asyncio.sleep(5)
        else:
            logger.error("Сервис не готов, завершаем")
            return
        
        logger.info("Сервис готов! Начинаем генерацию...")
        
        # Генерируем изображения
        for i, prompt in enumerate(prompts, 1):
            print(f"\n--- Генерация {i}/{len(prompts)} ---")
            
            image_data = await controller.generate_image(
                prompt=prompt,
                width=512,
                height=512,
                steps=20,
                guidance=7.5
            )
            
            if image_data:
                filename = f"image_{i:02d}_{prompt.replace(' ', '_')[:30]}.png"
                if await controller.save_image(image_data, filename):
                    print(f"✅ Готово: {filename}")
                else:
                    print(f"❌ Ошибка сохранения: {prompt}")
            else:
                print(f"❌ Ошибка генерации: {prompt}")
            
            # Небольшая пауза между запросами
            await asyncio.sleep(1)
        
        print(f"\n🎉 Генерация завершена! Проверьте папку 'output'")

if __name__ == "__main__":
    asyncio.run(main())