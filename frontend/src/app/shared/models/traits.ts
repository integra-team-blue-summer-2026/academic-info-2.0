import { TeacherDto } from '../../core/api/models/teacherDto';

/**
 * Trait-urile vin de la backend ca enum in SCREAMING_SNAKE_CASE
 * (ex. 'ARTIFICIAL_INTELLIGENCE'). Pentru UI vrem ceva citibil
 * ('Artificial Intelligence'), asa ca facem transformarea intr-un
 * singur loc si o refolosim si in vederea studentului, si oriunde
 * mai afisam traits.
 *
 * Cuvintele scurte (<= 2 litere, ex. 'SE') le lasam cu majuscule,
 * ca sa nu iasa 'Se' din 'SOFTWARE_ENGINEERING' -> 'EMPIRICAL_METHODS_SE'.
 */
export function traitLabel(value: string): string {
  return value
    .split('_')
    .map((word) =>
      word.length <= 2 ? word : word.charAt(0) + word.slice(1).toLowerCase(),
    )
    .join(' ');
}

/**
 * Lista pentru dropdown-ul de traits, generata direct din enum-ul
 * backend-ului. Daca backend-ul adauga un trait nou, apare automat aici.
 */
export const TRAIT_OPTIONS: { label: string; value: TeacherDto.TraitsEnum }[] =
  Object.values(TeacherDto.TraitsEnum).map((value) => ({
    label: traitLabel(value),
    value,
  }));
