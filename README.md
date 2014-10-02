# Stego-Hide

A standalone Java application capable of hiding files in images using a novel and robust technique.

### Steganographic Technique

The technique implemented in this application relies on pixel swapping within the image (spatial domain). The main idea is to divide the image into sub-blocks, where the data bits are embedded and extracted (1 bit per block) according to some criteria. First, the median of the pixel values for each block is computed. Then, the block's central pixel is compared to its median, if they have equal intensities, this will denote a data bit of value one, otherwise, the bit's value will be zero. Moreover, not all image blocks are eligible for embedding, only the ones with varying intensities among them, that is, a block having pixels of equal intensity is not suitable for embedding.

The embedding process starts by computing the eligible blocks in case of different block sizes, and the most suitable block size is chosen according to the data size to be hidden (as different block sizes results in different hide capacities). Then, the block selection order is randomized using a pseudo random generator with a secret key as its seed. After that, the data bits are embedded in the blocks, by rearranging the block's pixels according to the criteria mentioned above. Moreover, if pixels have to be swapped within blocks during embedding, the pixel with the nearest intensity to that of the block's central pixel is chosen for swapping.

This approach does not alter the image's intensity histogram, thus, it is more robust against steganalysis attacks relying on histogram statistics. At the same time, it allows for a moderate hide capacity, and leaves minimal imperceptible distortions compared to other techniques (e.g. LSB substitution).

### Download

[Version 1.0](https://github.com/bishoybassem/stego-hide/releases/download/v1.0/Stego-Hide.jar)

### Screenshots

![screen1](/screenshots/screen1.jpg)

![screen2](/screenshots/screen2.jpg)
