# video.audio.recorder.v2

Audio and video capturing software created for learning propose.
It captures bytes of audio from microphone and collection of images from camera with timestamp and thane play it. It iterates audio bytes while it timestamp residet between timspemps of two  ordered images.

Question - should player (images,(audio)*)* or (audio,(video)*)*? Second looks like better because it doesn't require special condion and statement to play last image element and skiping to play sound before first image of the video.
