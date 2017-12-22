package jp.ats.substrate.revision;

import java.util.Set;

/**
 * @author 千葉 哲嗣
 */
public class RevisionManager<T> {

	private final RevisionRepository<T> repository;

	public RevisionManager(RevisionRepository<T> repository) {
		this.repository = repository;
	}

	public Revision get(T target) {
		repository.lock(target);
		try {
			return repository.get(target);
		} finally {
			repository.unlock(target);
		}
	}

	public Revision next(
		T target,
		Revision targetRevision,
		Precondition precondition) throws OldRevisionException {
		repository.lock(target);
		try {
			Revision current = repository.get(target);

			if (current.getValue() != targetRevision.getValue()) throw new OldRevisionException();

			if (!precondition.execute()) return current;

			Revision next = current.increment();

			repository.put(target, next);

			return next;
		} finally {
			repository.unlock(target);
		}
	}

	public Revision remove(T target) {
		repository.lock(target);
		try {
			return repository.remove(target);
		} finally {
			repository.unlock(target);
		}
	}

	public void clear() {
		repository.lockAll();
		try {
			repository.clear();
		} finally {
			repository.unlockAll();
		}
	}

	public Set<T> keySet() {
		repository.lockAll();
		try {
			return repository.keySet();
		} finally {
			repository.unlockAll();
		}
	}
}
