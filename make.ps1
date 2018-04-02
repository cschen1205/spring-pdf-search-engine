
$profile="local"

$currentPath = $pwd

if($args.length -gt 0) {
    $profile=$args[0]
}

Invoke-Expression -Command:"mvn -f pom.xml clean package -P$profile -U"

$projs=@("pdf-search-engine")
foreach ($proj in $projs){
    $source=$PSScriptRoot + "/target/" + $proj + ".jar"
    $dest=$PSScriptRoot + "/" + $proj + ".jar"
    copy $source $dest
}

cd $currentPath
