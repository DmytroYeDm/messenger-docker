FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    x11-xkb-utils libfontconfig1 fonts-dejavu libfreetype6 \
    libpango-1.0-0 libpangoft2-1.0-0 libpangocairo-1.0-0 libcairo2 \
    libglib2.0-0 libgtk-3-0 libatk1.0-0 libgdk-pixbuf2.0-0 \
    libasound2 libuuid1 libxext6 libxrender1 libxtst6 \
    libgl1-mesa-glx libgl1-mesa-dri \
    && rm -rf /var/lib/apt/lists/*

COPY messenger.jar messenger.jar
CMD ["org.messenger.messenger.server.Launcher"]
ENTRYPOINT ["java", "-Dprism.order=sw", "-Djdk.gtk.version=3", "-cp", "messenger.jar"]