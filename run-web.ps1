param([int]$Port = 8080, [switch]$Build)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) {
    # Reuse the existing local IntelliJ credential; never write it to a new file.
    $settings = Join-Path $PSScriptRoot '.idea\workspace.xml'
    if (Test-Path -LiteralPath $settings) {
        [xml]$idea = Get-Content -LiteralPath $settings
        $config = $idea.project.component | Where-Object name -eq 'RunManager' |
            ForEach-Object { $_.configuration } | Where-Object name -eq 'AppLauncher'
        $env:DB_PASSWORD = ($config.envs.env | Where-Object name -eq 'DB_PASSWORD').value
    }
}
if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) {
    throw 'Set DB_PASSWORD to the Render database password before running this script.'
}
$jar = Join-Path $PSScriptRoot 'target\HK_Restaurant-1.0.0.jar'
if ($Build -or !(Test-Path -LiteralPath $jar)) {
    & mvn '-Dmaven.repo.local=target/maven-repository' package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
}
Write-Host "HK Restaurant web: http://localhost:$Port"
& java -jar $jar "--server.port=$Port"
exit $LASTEXITCODE
