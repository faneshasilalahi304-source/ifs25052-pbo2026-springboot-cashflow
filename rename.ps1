# Ganti username placeholder dengan username Anda.
# Pemakaian (PowerShell): .\rename.ps1 -NewUser username_anda
param([Parameter(Mandatory=$true)][string]$NewUser)
$old = 'username'
$files = Get-ChildItem -Path . -Recurse -File -Include *.java, pom.xml, *.yaml, *.md |
    Where-Object { $_.FullName -notlike '*\target\*' -and $_.FullName -notlike '*\.mvn\*' -and $_.FullName -notlike '*\node_modules\*' }
foreach ($f in $files) {
    $text = [System.IO.File]::ReadAllText($f.FullName)
    $new = $text.Replace("$old-pbo2026", "$NewUser-pbo2026").Replace("${old}pbo2026", "${NewUser}pbo2026")
    if ($new -ne $text) {
        [System.IO.File]::WriteAllText($f.FullName, $new, (New-Object System.Text.UTF8Encoding($false)))
    }
}
foreach ($base in 'src\main\java', 'src\test\java') {
    $dir = Join-Path $base "org\delcom\${old}pbo2026springbootcashflow"
    if (Test-Path $dir) {
        Rename-Item -Path $dir -NewName "${NewUser}pbo2026springbootcashflow"
    }
}
Write-Host "Selesai. Ganti nama folder proyek menjadi $NewUser-pbo2026-springboot-cashflow"
