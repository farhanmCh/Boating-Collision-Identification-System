import numpy as np
import matplotlib.pyplot as plt
import io
import cv2
from PIL import Image
import base64
import regex as re
import ast

def plot(data):
    '''
    This function takes in an array of data points in the form of string and converts it to a numpy array
    that is used to generate a depth image, which is is then converted to a byte string
    :param data:
     Takes in array of data points in string format
    :return:
    Returns a byte string of the of the image constructed using the data points in the array
    '''

    # Plot the data points
    fig = plt.figure(figsize=(6,4))
    ay = fig.subplots(1)
    ls = re.sub('\s+', ' ', data)
    a = np.array(ast.literal_eval(ls))
    depth = a.astype(np.uint8)
    p2 = ay.matshow(a, cmap='jet') # get original values for colorbar
    p = ay.matshow(depth, cmap='jet')
    plt.axis('off') # Remove axis

    plt.colorbar(p2, aspect=40, pad=0.02)
    fig.tight_layout()
    plt.show()
    fig.canvas.draw()

    # convert canvas data to cv2 image
    img = np.fromstring(fig.canvas.tostring_rgb(), dtype=np.uint8, sep='')
    img = img.reshape(fig.canvas.get_width_height()[::-1]+(3,))
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

    # Convert image to byte string
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())

    return "" + str(img_str, 'utf-8')