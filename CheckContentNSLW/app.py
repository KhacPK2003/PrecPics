from flask import Flask, request, jsonify
from transformers import AutoModelForImageClassification, ViTImageProcessor
from PIL import Image
import torch

app = Flask(__name__)

# Load model and processor
model = AutoModelForImageClassification.from_pretrained("Falconsai/nsfw_image_detection")
processor = ViTImageProcessor.from_pretrained("Falconsai/nsfw_image_detection")

@app.route('/classify', methods=['POST'])
def classify_image():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    # Open the image
    image = Image.open(file.stream)

    # Process image for the model
    inputs = processor(images=image, return_tensors="pt")

    # Run inference
    with torch.no_grad():
        outputs = model(**inputs)
        logits = outputs.logits

    # Get the predicted label
    predicted_label = logits.argmax(-1).item()
    label = model.config.id2label[predicted_label]

    return jsonify({"label": label})

if __name__ == '__main__':
    app.run(debug=True)
