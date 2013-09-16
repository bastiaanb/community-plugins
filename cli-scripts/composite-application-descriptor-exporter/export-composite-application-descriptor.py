import string

def exportCompositePackage(compositeName, targetFile):
  compositePackage = repository.read('Applications/' + compositeName)

  f = open(targetFile, 'w')
  try:
    [app, ver] = string.rsplit(compositePackage.id, "/", 1)
    f.write("application: %s\n" % (app))
    f.write("version: %s\n\n" % (ver))

    i = 1
    for package in compositePackage.packages:
      [app, ver] = string.rsplit(package, "/", 1)
      f.write("package.%s.name=%s\n" % (i, app))
      f.write("package.%s.version=%s\n\n" % (i, ver))
      i = i + 1
  finally:
    f.close()

  return
