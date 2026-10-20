import { useState } from "react";
import "./Consulta.css";

function Clientes() {
    const [activeTab, setActiveTab] = useState("consulta");
    const [entity, setEntity] = useState("products");
    const [results, setResults] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const search = async (event) => {
        event.preventDefault();
        setLoading(true);
        setError("");
        setResults(null);

        try {
            const response = await fetch(
                `http://localhost:8000/producer/query?type=${encodeURIComponent(entity)}`
            );

            if (!response.ok) {
                throw new Error("No fue posible completar la consulta.");
            }

            setResults(await response.json());
        } catch (requestError) {
            setError(requestError.message || "Ocurrió un error al consultar.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="consulta-page">
            <h1>Consulta</h1>
            <div className="consulta-tabs" role="tablist" aria-label="Secciones">
                <button
                    id="consulta-tab"
                    className={`consulta-tab ${activeTab === "consulta" ? "active" : ""}`}
                    type="button"
                    role="tab"
                    aria-selected={activeTab === "consulta"}
                    aria-controls="consulta-panel"
                    onClick={() => setActiveTab("consulta")}
                >
                    Consulta
                </button>
                <button
                    id="arquitectura-tab"
                    className={`consulta-tab ${activeTab === "arquitectura" ? "active" : ""}`}
                    type="button"
                    role="tab"
                    aria-selected={activeTab === "arquitectura"}
                    aria-controls="arquitectura-panel"
                    onClick={() => setActiveTab("arquitectura")}
                >
                    Arquitectura
                </button>
            </div>

            {activeTab === "consulta" ? (
                <section
                    id="consulta-panel"
                    className="consulta-panel"
                    role="tabpanel"
                    aria-labelledby="consulta-tab"
                >
                    <form className="consulta-form" onSubmit={search}>
                        <label htmlFor="consulta-entidad">Consultar</label>
                        <select
                            id="consulta-entidad"
                            value={entity}
                            onChange={(event) => setEntity(event.target.value)}
                        >
                            <option value="products">Productos</option>
                            <option value="clients">Clientes</option>
                        </select>
                        <button type="submit" disabled={loading}>
                            {loading ? "Consultando..." : "Consultar por Kafka"}
                        </button>
                    </form>

                    {error && <p className="consulta-error" role="alert">{error}</p>}
                    {results && (
                        <pre className="consulta-results" aria-live="polite">
                            {JSON.stringify(results, null, 2)}
                        </pre>
                    )}
                </section>
            ) : (
                <section
                    id="arquitectura-panel"
                    className="consulta-panel arquitectura-placeholder"
                    role="tabpanel"
                    aria-labelledby="arquitectura-tab"
                >
                    <p>Espacio reservado para colocar el diagrama de arquitectura.</p>
                </section>
            )}
        </main>
    );
}

export default Clientes