import pandas as pd
import tensorflow_hub as hub
from transformers import AutoFeatureExtractor, ASTForAudioClassification

ast_model_name = "MIT/ast-finetuned-audioset-10-10-0.4593"
ast_feature_extractor = AutoFeatureExtractor.from_pretrained(ast_model_name)
ast_model = ASTForAudioClassification.from_pretrained(ast_model_name)

class_map = pd.read_csv('yamnet_class_map.csv')
class_names = class_map['display_name'].to_dict()

yamnet_model = hub.load("https://tfhub.dev/google/yamnet/1")