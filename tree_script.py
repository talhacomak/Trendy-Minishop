#!/usr/bin/env python3
import argparse, os, fnmatch, sys
from pathlib import Path

def should_skip(name: str, ignore_names, ignore_globs):
    if name in ignore_names:
        return True
    return any(fnmatch.fnmatch(name, pat) for pat in ignore_globs)

def scan(dir_path: Path, max_depth: int, ignore_names, ignore_globs, prefix=""):
    try:
        items = sorted(list(dir_path.iterdir()), key=lambda p: (not p.is_dir(), p.name.lower()))
    except PermissionError:
        return []

    # Filter ignores
    items = [p for p in items if not should_skip(p.name, ignore_names, ignore_globs)]

    lines = []
    for idx, p in enumerate(items):
        is_last = (idx == len(items) - 1)
        connector = "└── " if is_last else "├── "
        lines.append(f"{prefix}{connector}{p.name}")
        if p.is_dir() and max_depth > 0:
            child_prefix = prefix + ("    " if is_last else "│   ")
            lines.extend(scan(p, max_depth - 1, ignore_names, ignore_globs, child_prefix))
    return lines

def main():
    parser = argparse.ArgumentParser(description="Print project tree with ignores")
    parser.add_argument("-r", "--root", default=".", help="Root directory")
    parser.add_argument("-d", "--depth", type=int, default=8, help="Max depth")
    parser.add_argument("-o", "--out", default="project_tree.txt", help="Output file")
    parser.add_argument("-e", "--ignore", nargs="*", default=["build", ".gradle", ".idea", ".git", "out", ".cxx", "captures"],
                        help="Exact names to ignore")
    parser.add_argument("-g", "--ignore-glob", nargs="*", default=["*.iml", ".DS_Store"],
                        help="Glob patterns to ignore (e.g., *.iml)")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    header = root.name
    lines = [header]
    lines += scan(root, args.depth, set(args.ignore), set(args.ignore_glob))

    Path(args.out).write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote tree to {args.out}")

if __name__ == "__main__":
    main()
