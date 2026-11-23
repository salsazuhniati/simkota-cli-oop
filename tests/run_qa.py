"""Compile and run the QA scenarios against the current Java sources."""

import subprocess
import tempfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
EVIDENCE = ROOT / "tests" / "evidence"


def clean_transcript(text):
    return "\n".join(line.rstrip() for line in text.splitlines()).rstrip() + "\n"


def run(command, **kwargs):
    return subprocess.run(command, text=True, encoding="utf-8", errors="replace", capture_output=True, **kwargs)


def main():
    EVIDENCE.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="simkota-qa-") as build:
        sources = sorted((ROOT / "simkota").glob("*.java")) + [ROOT / "tests" / "SimKotaQaTest.java"]
        compiled = run(["javac", "-encoding", "UTF-8", "-d", build, *map(str, sources)])
        if compiled.returncode:
            raise RuntimeError(compiled.stderr)

        result = run(["java", "-Dfile.encoding=UTF-8", "-cp", build, "SimKotaQaTest"])
        transcript = result.stdout + result.stderr
        (EVIDENCE / "qa-test-results.txt").write_text(clean_transcript(transcript), encoding="utf-8")
        print(transcript, end="")

        inputs = "QA\nKota Uji\n" + "6\n" * 15 + "n\n"
        main_result = run(["java", "-Dfile.encoding=UTF-8", "-cp", build, "Main"], input=inputs)
        reproduction = f"Exit code: {main_result.returncode}\n\n{main_result.stdout}\n{main_result.stderr}"
        (EVIDENCE / "main-stdin-reproduction.txt").write_text(clean_transcript(reproduction), encoding="utf-8")
        print(f"Main piped-input exit code: {main_result.returncode}")
        if main_result.returncode or "RONDE 15/15" not in main_result.stdout or "Terima kasih telah bermain" not in main_result.stdout:
            print("FAIL Main piped-input: permainan tidak selesai atau tidak keluar normal")
            return 1

        replay = run(["java", "-Dfile.encoding=UTF-8", "-cp", build, "Main"],
                     input="QA\nKota A\n" + "6\n" * 15 + "y\nQA\nKota B\n" + "6\n" * 15 + "n\n")
        if replay.returncode or replay.stdout.count("RONDE 15/15") != 2 or "Kota: Kota B" not in replay.stdout:
            print("FAIL Main replay: permainan kedua tidak selesai")
            return 1
        print("PASS Main piped-input dan bermain ulang")
        return result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
