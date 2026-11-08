"""Gera as imagens (pixel art) do Fish a Fish em ../assets.
Uso: python3 tools/gen_assets.py   (requer Pillow)
Para mudar o visual, edite as cores/formas aqui ou substitua os PNGs direto.
"""
import os
import random
from PIL import Image, ImageDraw

OUT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "assets"))
os.makedirs(OUT, exist_ok=True)
OUTLINE = (43, 38, 66, 255)


def outline(img, color=OUTLINE):
    w, h = img.size
    src = img.copy().load()
    px = img.load()
    for y in range(h):
        for x in range(w):
            if src[x, y][3] == 0:
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < w and 0 <= ny < h and src[nx, ny][3] > 0:
                        px[x, y] = color
                        break
    return img


def R(d, x0, y0, x1, y1, c):
    d.rectangle([x0, y0, x1, y1], fill=c)


# ---------------- Jogador (32x32) : pescador de chapéu de palha ----------------
def make_player():
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    skin, shirt = (245, 201, 160, 255), (232, 83, 74, 255)
    straw, straw2 = (242, 201, 76, 255), (222, 170, 50, 255)
    R(d, 11, 2, 17, 4, straw)          # topo do chapéu
    R(d, 11, 5, 17, 5, (200, 50, 50, 255))  # fita
    R(d, 7, 6, 21, 6, straw2)          # aba
    R(d, 9, 7, 18, 14, skin)           # cabeça
    R(d, 9, 7, 10, 10, (110, 70, 40, 255))  # cabelo
    R(d, 15, 9, 16, 10, (30, 30, 40, 255))  # olho
    R(d, 17, 12, 17, 12, (255, 150, 150, 255))  # bochecha
    R(d, 15, 13, 16, 13, (150, 60, 50, 255))    # boca
    R(d, 8, 15, 19, 23, shirt)         # camiseta
    R(d, 8, 19, 19, 19, (255, 255, 255, 255))   # listra
    R(d, 17, 17, 22, 19, shirt)        # braço
    R(d, 23, 17, 24, 18, skin)         # mão
    d.line([(23, 18), (30, 8)], fill=(139, 90, 43, 255))  # vara
    R(d, 9, 24, 13, 28, (52, 73, 94, 255))   # pernas
    R(d, 14, 24, 18, 28, (52, 73, 94, 255))
    R(d, 8, 29, 14, 30, (107, 62, 31, 255))  # sapatos
    R(d, 14, 29, 20, 30, (107, 62, 31, 255))
    return outline(img)


# ---------------- Peixe (32x16), olhando para a esquerda ----------------
def make_fish():
    img = Image.new("RGBA", (32, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    body, dark, belly = (244, 169, 58, 255), (205, 112, 30, 255), (255, 227, 161, 255)
    d.polygon([(24, 8), (30, 2), (30, 14)], fill=dark)      # cauda
    d.polygon([(11, 4), (15, 1), (19, 4)], fill=dark)       # nadadeira
    d.ellipse([4, 2, 25, 13], fill=body)                    # corpo
    d.ellipse([6, 8, 22, 12], fill=belly)                   # barriga
    R(d, 13, 4, 14, 11, dark)                               # listras
    R(d, 18, 4, 19, 11, dark)
    R(d, 7, 5, 9, 7, (255, 255, 255, 255))                  # olho
    R(d, 8, 6, 8, 6, (20, 20, 30, 255))
    R(d, 4, 8, 4, 8, (160, 50, 40, 255))                    # boca
    return outline(img)


# ---------------- Cenário (320x180) ----------------
def make_scene():
    W, H = 320, 180
    img = Image.new("RGBA", (W, H))
    d = ImageDraw.Draw(img)
    for y in range(90):  # céu
        t = y / 89
        d.line([(0, y), (W, y)], fill=(int(120 + 80 * t), int(200 + 35 * t), 255, 255))
    d.ellipse([259, 13, 277, 31], fill=(255, 240, 150, 255))  # sol
    d.ellipse([261, 15, 275, 29], fill=(255, 222, 90, 255))
    for cx, cy in ((30, 14), (140, 30), (215, 10)):  # nuvens
        d.ellipse([cx, cy + 4, cx + 32, cy + 13], fill=(255, 255, 255, 255))
        d.ellipse([cx + 8, cy, cx + 24, cy + 10], fill=(255, 255, 255, 255))
    d.ellipse([-40, 66, 90, 130], fill=(98, 168, 108, 255))   # colinas
    d.ellipse([200, 70, 350, 134], fill=(84, 152, 98, 255))
    d.ellipse([90, 76, 230, 134], fill=(112, 180, 120, 255))
    for y in range(90, H):  # água
        t = (y - 90) / 89
        d.line([(0, y), (W, y)], fill=(int(70 - 40 * t), int(160 - 70 * t), int(220 - 50 * t), 255))
    d.line([(0, 90), (W, 90)], fill=(160, 220, 250, 255))
    random.seed(7)
    for _ in range(90):  # ondinhas
        x, y, l = random.randint(0, W - 8), random.randint(93, H - 2), random.randint(3, 6)
        d.line([(x, y), (x + l, y)], fill=(125, 205, 245, 255))
    # píer
    for x in (6, 24, 44):
        R(d, x, 88, x + 2, 112, (90, 58, 30, 255))
    R(d, 0, 82, 52, 87, (150, 100, 55, 255))
    R(d, 0, 82, 52, 82, (195, 140, 80, 255))
    R(d, 0, 87, 52, 87, (95, 60, 30, 255))
    for x in range(8, 52, 8):
        R(d, x, 83, x, 86, (110, 70, 35, 255))
    return img


# ---------------- Menu (320x180): cenário + título + personagem ----------------
GLYPHS = {
    "F": ["11111", "10000", "11110", "10000", "10000", "10000", "10000"],
    "I": ["11111", "00100", "00100", "00100", "00100", "00100", "11111"],
    "S": ["01111", "10000", "10000", "01110", "00001", "00001", "11110"],
    "H": ["10001", "10001", "10001", "11111", "10001", "10001", "10001"],
    "A": ["01110", "10001", "10001", "11111", "10001", "10001", "10001"],
}


def draw_title(img, text, x0, y0, s=3):
    d = ImageDraw.Draw(img)
    for layer, off, col in (("sombra", 3, (30, 40, 90, 255)), ("frente", 0, (255, 214, 64, 255))):
        x = x0
        for ch in text:
            if ch == " ":
                x += 9
                continue
            for r, row in enumerate(GLYPHS[ch]):
                for c, bit in enumerate(row):
                    if bit == "1":
                        px, py = x + c * s + off, y0 + r * s + off
                        d.rectangle([px, py, px + s - 1, py + s - 1], fill=col)
            x += 5 * s + 3


def make_menu(player, fish):
    img = make_scene()
    p2 = player.resize((64, 64), Image.NEAREST)
    img.alpha_composite(p2, (6, 82 - 64))
    f2 = fish.resize((64, 32), Image.NEAREST).rotate(28, expand=True, resample=Image.NEAREST)
    img.alpha_composite(f2, (105, 62))
    draw_title(img, "FISH A FISH", 100, 8)
    return img


# ---------------- Botão (96x24) ----------------
def make_button():
    img = Image.new("RGBA", (96, 24), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    dark = (90, 55, 28, 255)
    R(d, 2, 0, 93, 23, dark)
    R(d, 0, 2, 95, 21, dark)
    R(d, 1, 1, 94, 22, dark)
    R(d, 2, 2, 93, 21, (222, 170, 100, 255))
    R(d, 3, 3, 92, 4, (245, 212, 150, 255))
    R(d, 3, 19, 92, 20, (182, 128, 68, 255))
    R(d, 5, 11, 5, 12, dark)
    R(d, 90, 11, 90, 12, dark)
    return img


if __name__ == "__main__":
    player, fish = make_player(), make_fish()
    player.save(os.path.join(OUT, "player.png"))
    fish.save(os.path.join(OUT, "fish.png"))
    make_scene().save(os.path.join(OUT, "scenery.png"))
    make_menu(player, fish).save(os.path.join(OUT, "menu.png"))
    make_button().save(os.path.join(OUT, "button.png"))
    print("Imagens geradas em", OUT)
