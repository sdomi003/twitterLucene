from mechanize import Browser
br = Browser()
br.set_handle_robots(False)
url = "https://t.co/x4lLaQZsCQ"
br.open(url)
print (br.title())
