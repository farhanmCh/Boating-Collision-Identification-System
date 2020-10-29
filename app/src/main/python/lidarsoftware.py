import numpy as np 
import matplotlib.pyplot as plt
import io
import cv2
from PIL import Image
import base64

def plot():
    # Random XYZ data
    xyz=np.array(np.random.uniform(-20, 20, (9000,50)))

    # Plot the data points
    fig = plt.figure(figsize=(6,4))
    ay = fig.subplots(1)
    p = ay.scatter(xyz[:,0], xyz[:,1], c=xyz[:,2], cmap='jet', s=1) 
    plt.colorbar(p, aspect=40, pad=0.02)
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
